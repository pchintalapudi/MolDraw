/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package moldraw.xml;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 *
 * @author prem
 */
public interface XMLData extends Serializable {

    String element();

    String data();

    Map<String, String> attributes();

    List<XMLData> nested();
}
