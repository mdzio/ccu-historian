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

import java.awt.Paint
import org.jfree.chart.ChartColor
import groovy.transform.CompileStatic

@CompileStatic
class TrendDesign {

	private static Paint[] defaultColors=(Paint[])[
		ChartColor.DARK_RED, ChartColor.DARK_BLUE, ChartColor.DARK_GREEN,
		ChartColor.DARK_MAGENTA, ChartColor.DARK_CYAN, ChartColor.DARK_YELLOW, 
		ChartColor.DARK_GRAY,
		ChartColor.RED, ChartColor.BLUE, ChartColor.GREEN,
		ChartColor.MAGENTA, ChartColor.CYAN, ChartColor.YELLOW, 
		ChartColor.GRAY, ChartColor.PINK
	].toArray(new Paint[0])

	String identifier
	String displayName
	Closure chart
	Closure plot
	Closure timeAxis
	List<Closure> rangeAxes
	List<Closure> series
	List<Closure> renderers

	static Paint getDefaultColor(int idx) {
		defaultColors[idx%defaultColors.length]
	}
}
