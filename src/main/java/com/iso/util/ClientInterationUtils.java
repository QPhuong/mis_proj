package com.iso.util;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import org.primefaces.component.datatable.DataTable;

public class ClientInterationUtils {

	private static DataTable getDataTable(UIComponent source, String tableId) {
		UIComponent table = FacesContext.getCurrentInstance().getViewRoot().findComponent(":form:cars");
		return (DataTable) table;
		
	}
	public static DataTable resetTable(UIComponent source, String tableId) {
        DataTable table = getDataTable(source, tableId);
        if (table != null) {
            /*// reset table state
            ValueExpression ve = table.getValueExpression("sortBy");
            if (ve != null) {
                table.setValueExpression("sortBy", null);
            }

            ve = table.getValueExpression("filterBy");
            if (ve != null) {
                table.setValueExpression("filterBy", null);
            }

            ve = table.getValueExpression("filteredValue");
            if (ve != null) {
                table.setValueExpression("filteredValue", null);
            }
*/
        	
        	table.setFilteredValue(null);
            table.reset();
        }

        return table;
    } 
}
