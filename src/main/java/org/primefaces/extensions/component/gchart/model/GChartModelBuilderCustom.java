package org.primefaces.extensions.component.gchart.model;

import org.apache.commons.lang3.StringUtils;
import org.primefaces.model.TreeNode;

import com.iso.domain.OrganizationUnit;

public class GChartModelBuilderCustom extends GChartModelBuilder{

	public GChartModelBuilderCustom() {
		super();
	}
	
	@Override
	public GChartModelBuilder importTreeNode(TreeNode root){
		
		String label;
		if (root.getData() instanceof OrganizationUnit) {
			OrganizationUnit orgUnit = (OrganizationUnit) root.getData();
			label = orgUnit.getName();
		}else {
			label = String.valueOf(root.getData());
		}
		
		String parentLabel = StringUtils.EMPTY;
		if (root.getParent() != null) {
			if (root.getParent().getData() instanceof OrganizationUnit) {
				OrganizationUnit orgParentUnit = (OrganizationUnit) root.getParent().getData();
				parentLabel = orgParentUnit.getName();
			}else {
				parentLabel = String.valueOf(root.getParent().getData());
			}
		}
        this.addRow(label,parentLabel);

        for(TreeNode node : root.getChildren()){
            this.importTreeNode(node);
        }

        return this;
	}
	
	
}
