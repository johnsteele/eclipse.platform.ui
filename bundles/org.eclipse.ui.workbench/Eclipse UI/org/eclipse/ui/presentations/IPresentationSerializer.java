/*******************************************************************************
 * Copyright (c) 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.ui.presentations;

/**
 * This interface is given to a StackPresentation when it is loading or saving
 * its state.
 * 
 * Not intended to be implemented by clients
 * 
 * @since 3.0
 */
public interface IPresentationSerializer {
	/**
	 * Returns a unique identifier for the given part. The identifier can later
	 * be used to restore the original part by calling getPart(...). This identifier 
	 * is guaranteed to be unique within a particular StackPresentation. However, 
	 * the same part may be assigned a different ID each time the presentation is saved. 
	 * 
	 * @param part a part to be identified (not null)
	 * @return a unique identifier for the part (not null)
	 */
	public String getId(IPresentablePart part);
	
	/**
	 * Returns a presentable part, given an id that was generated when the presentation 
	 * was saved.
	 * 
	 * @param id an ID that was generated by getId(IPresentablePart) when the presentation
	 * was saved
	 * @return the presentable part associated with the given id, or null if none. Note
	 * that even if the ID was valid when the presentation was saved, it may not
	 * be valid when the presentation is restored. Callers must be prepared
	 * to handle a null result.
	 */
	public IPresentablePart getPart(String id);
}
