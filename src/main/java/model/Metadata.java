/************************************************************************
 * 				iSBatch  Copyright (C) 2015  							*
 *		Victor E. A. Caldas -  v.e.a.caldas at rug.nl					*
 *		C. Michiel Punter - c.m.punter at rug.nl						*
 *																		*
 *	This program is distributed in the hope that it will be useful,		*
 *	but WITHOUT ANY WARRANTY; without even the implied warranty of		*
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the		*
 *	GNU General Public License for more details.						*
 *	You should have received a copy of the GNU General Public License	*
 *	along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ***********************************************************************/

package model;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

// TODO: Auto-generated Javadoc
/**
 * The Class Metadata.
 */
public class Metadata {
	
	/** The metadata. */
	private Map<String, Object> metadata;
	
	/**
	 * Instantiates a new metadata.
	 *
	 * @param metadata the metadata
	 */
	public Metadata(Map<String, Object> metadata){
		if(metadata==null){
			this.metadata = new HashMap<String, Object>();
		}
		else{
			this.metadata = new HashMap<String, Object>(metadata);
		}
	}
	
	public Metadata() {
		this.metadata = new HashMap<String, Object>();
	}

	/**
	 * Gets the property.
	 *
	 * @param propertyName the property name
	 * @return the property
	 */
	public Object getProperty(String propertyName ){
		return metadata.get(propertyName);
		}
	
	/**
	 * Gets the properties.
	 *
	 * @return the properties
	 */
	public Map<String, Object> getProperties(){
		return metadata;
	}
	
	/**
	 * Matches.
	 *
	 * @param otherProperties the other properties
	 * @return true, if successful
	 */
	public boolean matches(Metadata otherProperties){
		Iterator<String> i = ((Map<String, Object>) otherProperties.getProperties()).keySet().iterator(); i.hasNext(); {
		      String propertyName = (String)i.next();
		      if (!metadata.get(propertyName).equals(
		    		  otherProperties.getProperty(propertyName))) {
		        return false;
		      }
		    }
		    return true;
		  }
	
}
