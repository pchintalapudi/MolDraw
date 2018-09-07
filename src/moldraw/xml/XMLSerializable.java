/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package moldraw.xml;

import moldraw.utils.Identifiable;

/**
 *
 * @author prem
 */
public interface XMLSerializable extends Identifiable {

    XMLData serialize();

    @Override
    boolean equals(Object other);

    @Override
    int hashCode();

    public static boolean equals(XMLSerializable obj, Object other) {
        return other instanceof XMLSerializable && obj.getId().equals(((XMLSerializable) other).getId());
    }

    public static int hashCode(XMLSerializable obj) {
        return obj.getId().hashCode();
    }
}
