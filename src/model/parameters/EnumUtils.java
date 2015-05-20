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
package model.parameters;

/**
 * The Class EnumUtils. 
 * Several methods to perform comparisons with enumerations
 */
public class EnumUtils {

    /**
     * Contains
     * Check if the input name is contained in the Enum
     *
     * @param enumValues Enumeration values 
     * @param nameToCheck Name for comparison
     * @return true, if successful
     */
    @SuppressWarnings("rawtypes")
	public static boolean contains(Enum[] enumValues, String nameToCheck) {

        for(Enum each : enumValues) {
            if(each.toString().equals(nameToCheck)) {
                return true;
            }
        }
        return false;
   }
    
    /**
     * Contains
     *Check if the input name is contained in the Enum
     *
     * @param <T> the generic type
     * @param clazz The enum class (Enum.class)
     * @param nameToCheck  String for comparison
     * @return true, if successful
     */
    public static <T extends Enum<T>> boolean contains(Class<T> clazz, String nameToCheck) {
        try {
           Enum.valueOf(clazz, nameToCheck.toUpperCase());
        } catch (IllegalArgumentException e) {
            return false;
        }
        return true;
    }
    

    
    /**
     * The main method.
     *
     * @param args the arguments
     */
    public static void main(String[] args) {
//		System.out.println(Channel.GREEN.toString());
//    	System.out.println(Channel.valueOf(Channel.class, Channel.GREEN.toString()));
    	System.out.println(EnumUtils.contains(Channel.class, "Green"));
	}
    

}