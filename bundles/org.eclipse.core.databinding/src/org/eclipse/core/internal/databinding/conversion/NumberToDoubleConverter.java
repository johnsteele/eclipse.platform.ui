/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 ******************************************************************************/

package org.eclipse.core.internal.databinding.conversion;

import com.ibm.icu.text.NumberFormat;

/**
 * Converts from a Number to a Double.
 * <p>
 * Class is thread safe.
 * </p>
 * 
 * @since 1.0
 */
public class NumberToDoubleConverter extends NumberToNumberConverter {

	/**
	 * @param numberFormat
	 * @param fromType
	 * @param primitive
	 */
	public NumberToDoubleConverter(NumberFormat numberFormat, Class fromType,
			boolean primitive) {
		super(numberFormat, fromType, (primitive) ? Double.TYPE : Double.class);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.internal.databinding.conversion.NumberToNumberConverter#doConvert(java.lang.Number)
	 */
	protected Number doConvert(Number number) {
		if (StringToNumberParser.inDoubleRange(number)) {
			return new Double(number.doubleValue());
		}
		
		return null;
	}
}
