/*
    CCU-Historian, a long term archive for the HomeMatic CCU
    Copyright (C) 2011-2022 MDZ (info@ccu-historian.de)

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
package mdz.hc.timeseries.expr;

public class ExpressionCategory {

	public static Expression plus(Number n, Expression e) {
		return Expressions.from(n.doubleValue()).plus(e);
	}

	public static Expression minus(Number n, Expression e) {
		return Expressions.from(n.doubleValue()).minus(e);
	}

	public static Expression multiply(Number n, Expression e) {
		return Expressions.from(n.doubleValue()).multiply(e);
	}

	public static Expression div(Number n, Expression e) {
		return Expressions.from(n.doubleValue()).div(e);
	}
}
