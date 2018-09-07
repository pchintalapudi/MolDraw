/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package moldraw.model.bonds;

import java.util.function.Function;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import moldraw.utils.IDLogger;
import moldraw.xml.XMLData;
import moldraw.xml.XMLDataBuilder;
import moldraw.xml.XMLSerializable;

public class BondImpl implements Bond {

    private final ObjectProperty<Bondable> startBondable = new SimpleObjectProperty<>(),
            endBondable = new SimpleObjectProperty<>();
    private final ObjectProperty<BondState> bondStateProperty = new SimpleObjectProperty<>(BondState.SINGLE);
    private final ObjectProperty<VisualState> visualStateProperty = new SimpleObjectProperty<>(VisualState.NONE);

    private static int count;
    private String id;

    private BondImpl() {
    }

    public BondImpl(IDLogger idLogger) {
        this();
        id = idLogger.forceAdd("Bond " + (count = count == Integer.MAX_VALUE ? 0 : count + 1));
    }

    @Override
    public ObjectProperty<Bondable> startBondableProperty() {
        return startBondable;
    }

    @Override
    public ObjectProperty<Bondable> endBondableProperty() {
        return endBondable;
    }

    @Override
    public ObjectProperty<BondState> bondStateProperty() {
        return bondStateProperty;
    }

    @Override
    public ObjectProperty<VisualState> visualStateProperty() {
        return visualStateProperty;
    }

    @Override
    public XMLData serialize() {
        return XMLDataBuilder.newInstance().setElement("Bond").setData(id).addAttribute("start", getStartBondable().getId())
                .addAttribute("end", getEndBondable().getId()).addAttribute("state", getBondState())
                .addAttribute("visual-state", getVisualState()).build();
    }

    public static Bond deserialize(XMLData data, Function<String, Bondable> bondableFetcher) {
        if (!"Bond".equals(data.element())) {
            throw new IllegalArgumentException("Not a bond!!!");
        }
        BondImpl b = new BondImpl();
        b.setId(data.data());
        data.attributes().entrySet().forEach(e -> {
            switch (e.getKey()) {
                case "start":
                    b.setStartBondable(bondableFetcher.apply(e.getValue()));
                    break;
                case "end":
                    b.setEndBondable(bondableFetcher.apply(e.getValue()));
                    break;
                case "state":
                    b.setBondState(BondState.valueOf(e.getValue()));
                    break;
                case "visual-state":
                    b.setVisualState(VisualState.valueOf(e.getValue()));
                    break;
            }
        });
        return b;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object other) {
        return XMLSerializable.equals(this, other);
    }

    @Override
    public int hashCode() {
        return XMLSerializable.hashCode(this);
    }
}
