/*
    CCU-Historian, a long term archive for the HomeMatic CCU
    Copyright (C) 2011-2017 MDZ (info@ccu-historian.de)

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
package mdz.ccuhistorian

import static org.junit.Assert.*

import org.junit.Test

import groovy.sql.Sql
import mdz.hc.DataPoint
import mdz.hc.DataPointIdentifier

/**
 * Tests database consistency handling (see GitHub issue #453): history tables
 * are created before the DATA_POINTS entry refers to them (table first, entry
 * second), and history tables missing from an inconsistent database are
 * recreated at the next database connect.
 */
class DatabaseIntegrityTest {

	private static final String TABLE_NAME='D_CUXD_CUX2222_2_STATE'

	private File createTempDir() {
		File.createTempFile('ccuhistorian-test', '').with { delete(); mkdirs(); it }
	}

	private DatabaseConfig createConfig(File dir) {
		String dirName=dir.absolutePath.replace('\\', '/') + '/'
		new DatabaseConfig(dir:dirName, name:'history', webEnable:false)
	}

	private DataPoint createDataPoint() {
		DataPoint dp=new DataPoint()
		dp.id=new DataPointIdentifier('CUxD', 'CUX2222:2', 'STATE')
		dp.attributes.paramSet='VALUES'
		dp
	}

	private Sql createJdbcConnection(DatabaseConfig config) {
		Sql.newInstance(
			"jdbc:h2:file:${config.dir}${config.name};MODE=PostgreSQL;DB_CLOSE_ON_EXIT=FALSE;BUILTIN_ALIAS_OVERRIDE=TRUE",
			config.user, config.password, 'org.h2.Driver')
	}

	@Test
	public void testMissingHistoryTable() {
		File dir=createTempDir()
		DatabaseConfig config=createConfig(dir)
		Database db=new Database(config, null)
		Sql sql=null
		try {
			// create data point and insert one entry
			DataPoint dp=createDataPoint()
			db.createDataPoint(dp)
			assertEquals TABLE_NAME, dp.historyTableName
			sql=createJdbcConnection(config)
			sql.executeInsert '''INSERT INTO D_CUXD_CUX2222_2_STATE (TS, "VALUE", STATE) VALUES (?, ?, ?)''',
				[new java.sql.Timestamp(1000), 1.5d, 0]
			assertEquals 1, db.getCount(dp, null, null)

			// simulate the inconsistency of issue #453: entry without table
			sql.execute "DROP TABLE $TABLE_NAME" as String

			// self-healing: table is recreated at the next connect
			db.stop()
			db=new Database(config, null)
			assertEquals 1, sql.firstRow(
				"SELECT COUNT(*) FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA='PUBLIC' AND TABLE_NAME=?",
				TABLE_NAME)[0]
			assertEquals 0, db.getCount(dp, null, null)

			// recording continues seamlessly
			sql.executeInsert '''INSERT INTO D_CUXD_CUX2222_2_STATE (TS, "VALUE", STATE) VALUES (?, ?, ?)''',
				[new java.sql.Timestamp(2000), 2.5d, 0]
			assertEquals 1, db.getCount(dp, new Date(1500), null)
		} finally {
			db.stop()
			sql?.close()
			dir.deleteDir()
		}
	}

	@Test
	public void testCreateAndUpdateDataPoint() {
		File dir=createTempDir()
		DatabaseConfig config=createConfig(dir)
		Database db=new Database(config, null)
		Sql sql=null
		try {
			// create data point: table exists before the entry refers to it
			DataPoint dp=createDataPoint()
			db.createDataPoint(dp)
			sql=createJdbcConnection(config)
			assertEquals 1, sql.firstRow(
				"SELECT COUNT(*) FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA='PUBLIC' AND TABLE_NAME=?",
				TABLE_NAME)[0]

			// value type change: new table is created and entry is updated
			dp.historyString=true
			db.updateDataPoint(dp)
			assertEquals 'C_CUXD_CUX2222_2_STATE', dp.historyTableName
			assertEquals 1, sql.firstRow(
				"SELECT COUNT(*) FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA='PUBLIC' AND TABLE_NAME='C_CUXD_CUX2222_2_STATE'")[0]
			assertEquals 'C_CUXD_CUX2222_2_STATE',
				sql.firstRow('SELECT TABLE_NAME FROM DATA_POINTS WHERE DP_ID=?', dp.idx).TABLE_NAME

			// old table is still intact
			assertEquals 1, sql.firstRow(
				"SELECT COUNT(*) FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA='PUBLIC' AND TABLE_NAME=?",
				TABLE_NAME)[0]
		} finally {
			db.stop()
			sql?.close()
			dir.deleteDir()
		}
	}
}
