/**
 * Copyright (C) 2017 Wuhan University
 * 
 * This program is free software; you can redistribute and/or modify it under 
 * the terms of the GNU General Public License version 2 as published by the 
 * Free Software Foundation.
 * 
 * This program is distributed WITHOUT ANY WARRANTY; even without the implied
 * WARRANTY OF MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 */
package cn.edu.whu.opso;

import com.geojmodelbuilder.core.instance.impl.WorkflowInstance;

/**
 * Hello world!
 *
 */
public class HelloWorld 
{
    public static void main( String[] args )
    {
    	WorkflowInstance workflowInstance = new WorkflowInstance();
    	workflowInstance.setName("Hello World");
        System.out.println(workflowInstance.getName());
    }
}
