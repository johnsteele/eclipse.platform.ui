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
package org.eclipse.e4.ui.model.application.ui.menu;

import org.eclipse.e4.ui.model.application.ui.MElementContainer;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Contribution</b></em>'.
 * <!-- end-user-doc -->
 *
 * <!-- begin-model-doc -->
 * <p>
 * Provisional for 4.3.
 * </p>
 * @noreference This interface is not intended to be referenced by clients
 * @since 1.0
 * <!-- end-model-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.e4.ui.model.application.ui.menu.MMenuContribution#getPositionInParent <em>Position In Parent</em>}</li>
 *   <li>{@link org.eclipse.e4.ui.model.application.ui.menu.MMenuContribution#getParentId <em>Parent Id</em>}</li>
 * </ul>
 * </p>
 *
 * @model
 * @generated
 */
public interface MMenuContribution extends MElementContainer<MMenuElement> {
	/**
	 * Returns the value of the '<em><b>Position In Parent</b></em>' attribute.
	 * The default value is <code>""</code>.
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
	 * @return the value of the '<em>Position In Parent</em>' attribute.
	 * @see #setPositionInParent(String)
	 * @model default=""
	 * @generated
	 */
	String getPositionInParent();

	/**
	 * Sets the value of the '{@link org.eclipse.e4.ui.model.application.ui.menu.MMenuContribution#getPositionInParent <em>Position In Parent</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Position In Parent</em>' attribute.
	 * @see #getPositionInParent()
	 * @generated
	 */
	void setPositionInParent(String value);

	/**
	 * Returns the value of the '<em><b>Parent Id</b></em>' attribute.
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
	 * @return the value of the '<em>Parent Id</em>' attribute.
	 * @see #setParentId(String)
	 * @model required="true"
	 * @generated
	 */
	String getParentId();

	/**
	 * Sets the value of the '{@link org.eclipse.e4.ui.model.application.ui.menu.MMenuContribution#getParentId <em>Parent Id</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Parent Id</em>' attribute.
	 * @see #getParentId()
	 * @generated
	 */
	void setParentId(String value);

} // MMenuContribution
