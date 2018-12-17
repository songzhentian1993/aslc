/*
Copyright (C) 2006 Timo Tuunanen

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
*/
package lice.objects;

public class Dependency {

	public static String DEP_TYPE_UNKNOWN = "dep_type_unknown";
	//public static String DEP_TYPE_SOURCE = "dep_type_source";
	public static String DEP_TYPE_RPC = "dep_type_rpc";
	public static String DEP_TYPE_STATIC_LINK = "dep_type_static_link";
	public static String DEP_TYPE_DYNAMIC_LINK = "dep_type_dynamic_link";

	public Dependency ( TargetFile file, String type ) {
		this.targetFile = file;
		this.dependencyType = type;
	}
	
	public String getDependencyType() {
		return dependencyType;
	}

/*	public void setDependencyType(String dependencyType) {
		this.dependencyType = dependencyType;
	} */
	
	public TargetFile getTargetFile() {
		return targetFile;
	}
	
	private String dependencyType = DEP_TYPE_UNKNOWN;
	private TargetFile targetFile = null;
}
