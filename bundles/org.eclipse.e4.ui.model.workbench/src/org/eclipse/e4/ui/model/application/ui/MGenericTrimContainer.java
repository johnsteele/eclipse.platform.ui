/**
 * Copyright (c) 2008, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *      IBM Corporation - initial API and implementation
 */
package org.eclipse.e4.ui.model.application.ui;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Generic Trim Container</b></em>'.
 * <!-- end-user-doc -->
 *
 * <!-- begin-model-doc -->
 * <p>
 * <strong>Developers</strong>:
 * Add more detailed documentation by editing this comment in 
 * org.eclipse.ui.model.workbench/model/UIElements.ecore. 
 * There is a GenModel/documentation node under each type and attribute.
 * </p>
 * @since 1.0
 * <!-- end-model-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.e4.ui.model.application.ui.MGenericTrimContainer#getSide <em>Side</em>}</li>
 * </ul>
 * </p>
 *
 * @model abstract="true"
 * @generated
 */
public interface MGenericTrimContainer<T extends MUIElement> extends MElementContainer<T> {
	/**
	 * Returns the value of the '<em><b>Side</b></em>' attribute.
	 * The literals are from the enumeration {@link org.eclipse.e4.ui.model.application.ui.SideValue}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * <p>
	 * <strong>Developers</strong>:
	 * Add more detailed documentation by editing this comment in 
	 * org.eclipse.ui.model.workbench/model/UIElements.ecore. 
	 * There is a GenModel/documentation node under each type and attribute.
	 * </p>
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Side</em>' attribute.
	 * @see org.eclipse.e4.ui.model.application.ui.SideValue
	 * @see #setSide(SideValue)
	 * @model required="true"
	 * @generated
	 */
	SideValue getSide();

	/**
	 * Sets the value of the '{@link org.eclipse.e4.ui.model.application.ui.MGenericTrimContainer#getSide <em>Side</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Side</em>' attribute.
	 * @see org.eclipse.e4.ui.model.application.ui.SideValue
	 * @see #getSide()
	 * @generated
	 */
	void setSide(SideValue value);

} // MGenericTrimContainer
