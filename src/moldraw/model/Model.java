/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package moldraw.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javafx.geometry.Point2D;
import javafx.geometry.Point3D;
import javafx.util.Pair;
import moldraw.model.bonds.Bond;
import moldraw.model.bonds.BondImpl;
import moldraw.model.bonds.Bondable;
import moldraw.model.targets.Atom;
import moldraw.model.targets.RGroup;
import moldraw.model.targets.Element;
import moldraw.utils.IDLogger;
import moldraw.utils.Identifiable;
import moldraw.utils.Utils;
import moldraw.xml.XMLData;
import moldraw.xml.XMLDataBuilder;
import moldraw.xml.XMLSerializable;

/**
 *
 * @author prem
 */
public class Model implements XMLSerializable {

    private final Set<RGroup> rGroups = new HashSet<>();
    private final Set<Bond> bonds = new HashSet<>();
    private final Set<RGroup> deleted = Collections.newSetFromMap(new WeakHashMap<>());

    private String id;

    private final Set<RGroup> selected = new HashSet<>();
    private final IDLogger idLogger;

    private Model(IDLogger idLogger) {
        this.idLogger = idLogger;
    }

    public static Model create() {
        return new Model(new IDLogger());
    }

    public static Model create(RGroup[] rGroups, Bond[] bonds, Model parent) {
        Model m = new Model(parent.idLogger);
        Arrays.stream(rGroups).forEach(m::add);
        Arrays.stream(bonds).forEach(m::add);
        return m;
    }

    public Set<RGroup> getBondables() {
        return new HashSet<>(rGroups);
    }

    public Set<Bond> getBonds() {
        return new HashSet<>(bonds);
    }

    public Atom createAtom(Element e) {
        return new Atom(e, idLogger);
    }

    public Set<Bond> delete(RGroup r) {
        if (deleteWithoutBondDeletion(r)) {
            Set<Bond> deletedBonds = bonds.stream().filter(b -> b.contains(r)).collect(Collectors.toSet());
            bonds.removeAll(deletedBonds);
            return deletedBonds;
        }
        return new HashSet<>();
    }

    private boolean deleteWithoutBondDeletion(RGroup r) {
        if (rGroups.remove(r)) {
            selected.remove(r);
            deleted.add(r);
            return true;
        }
        return false;
    }

    public boolean add(RGroup r) {
        if (rGroups.add(r)) {
            deleted.remove(r);
            return true;
        }
        return false;
    }

    public void replace(RGroup replaced, RGroup replacer) {
        if (add(replacer)) {
            deleteWithoutBondDeletion(replaced);
        }
        bonds.stream().filter(b -> b.getStartBondable() == replaced).forEach(b -> b.setStartBondable(replacer));
        bonds.stream().filter(b -> b.getEndBondable() == replaced).forEach(b -> b.setEndBondable(replacer));
    }

    public Runnable join(RGroup replace, RGroup shifted, boolean getUndoOp) {
        deleteWithoutBondDeletion(replace);
        Set<Bond> starts = bonds.stream().filter(b -> replace.equals(b.getStartBondable()))
                .peek(b -> b.setStartBondable(shifted)).collect(Collectors.toSet());
        Set<Bond> ends = bonds.stream().filter(b -> replace.equals(b.getEndBondable()))
                .peek(b -> b.setEndBondable(shifted)).collect(Collectors.toSet());
        return getUndoOp ? () -> {
            add(replace);
            starts.forEach(b -> b.setStartBondable(replace));
            ends.forEach(b -> b.setEndBondable(replace));
        } : null;
    }

    public boolean setCenter(RGroup rgroup, double x, double y, double z) {
        if (!selected.contains(rgroup)) {
            rgroup.setCenter(x, y, z);
            return true;
        }
        return false;
    }

    public boolean shift(RGroup rgroup, double x, double y, double z) {
        if (!rgroup.isSelected()) {
            doShift(rgroup, x, y, z);
            return false;
        } else {
            selected.forEach(rg -> doShift(rg, x, y, z));
            return true;
        }
    }

    public boolean shift(RGroup rgroup, double x, double y) {
        if (!rgroup.isSelected()) {
            doShift(rgroup, x, y, 0);
            return false;
        } else {
            selected.forEach(rg -> doShift(rg, x, y, 0));
            return true;
        }
    }

    private void doShift(RGroup rgroup, double x, double y, double z) {
        rgroup.shiftCenter(x, y, z);
    }

    public void rotateSelected(double radians) {
        selected.stream().map(RGroup::getCenter).mapToDouble(Point3D::getX).average().ifPresent(x -> {
            selected.stream().map(RGroup::getCenter).mapToDouble(Point3D::getY).average().ifPresent(y -> {
                selected.forEach(r -> {
                    Point3D old = r.getCenter();
                    Point2D rotated = Utils.rotate(old.getX(), old.getY(), x, y, radians);
                    r.setCenter(rotated.getX(), rotated.getY(), old.getZ());
                });
            });
        });
    }

    public void deselectAll() {
        selected.forEach(rg -> rg.setSelected(false));
        selected.clear();
    }

    public void deselect(RGroup rg) {
        if (selected.remove(rg)) {
            rg.setSelected(false);
        }
    }

    public void select(RGroup rg) {
        if (selected.add(rg)) {
            rg.setSelected(true);
        }
    }

    public void selectAll(Collection<? extends RGroup> c) {
        c.forEach(this::select);
    }

    public void selectAll() {
        selectAll(rGroups);
    }

    public boolean toggleSelected(RGroup rg) {
        return selected.add(rg) || !selected.remove(rg);
    }

    public Set<RGroup> getSelectedCopy() {
        return new HashSet<>(selected);
    }

    @SuppressWarnings("element-type-mismatch")
    public Set<Bond> getSelectedBonds(boolean strict) {
        return bonds.stream().filter(b -> strict ? selected.contains(b.getStartBondable())
                && selected.contains(b.getEndBondable()) : selected.contains(b.getStartBondable())
                || selected.contains(b.getEndBondable())).collect(Collectors.toSet());
    }

    @SuppressWarnings("element-type-mismatch")
    public Map<String, List<XMLData>> serializeSelected() {
        Set<RGroup> selectedCopy = getSelectedCopy();
        Map<String, List<XMLData>> serialized = Stream.of("Atom", "RGroup")
                .collect(Collectors.toMap(Function.identity(), str -> new ArrayList<>()));
        selectedCopy.forEach(r -> serialized.get(r instanceof Atom ? "Atom" : "RGroup").add(r.serialize()));
        serialized.put("Bond", getSelectedBonds(true).stream().map(Bond::serialize).collect(Collectors.toList()));
        return serialized;
    }

    public Pair<Bond, Atom> getBond(RGroup start, Element e) {
        Bond b = new BondImpl(idLogger);
        b.setStartBondable(start);
        Atom a = createAtom(e);
        b.setEndBondable(a);
        a.setCenter(start.getCenter());
        add(b);
        add(a);
        return new Pair<>(b, a);
    }

    public void delete(Bond b) {
        bonds.remove(b);
    }

    public void add(Bond b) {
        bonds.add(b);
    }

    public void correctionShift(double x, double y, double z) {
        Stream.concat(rGroups.stream(), deleted.stream())
                .forEach(r -> r.shiftCenter(x, y, z));
    }

    public boolean validateJoin(Bondable b1, Bondable b2) {
        return rGroups.stream().filter(r -> r.equals(b1) || r.equals(b2)).count() == 2
                && !concurrentWalk(generateBondGraph(), Collections.newSetFromMap(new ConcurrentHashMap<>()), b1, b2);
    }

    //Recursively walk the bond graph in parallel to determine if the two bondables are connected
    private boolean concurrentWalk(Map<Bondable, Set<Bondable>> bondGraph, Set<Bondable> results, Bondable b1, Bondable check) {
        return bondGraph.get(b1).parallelStream().filter(results::add)
                .anyMatch(b -> check.equals(b) || concurrentWalk(bondGraph, results, b, check));
    }

    private Map<Bondable, Set<Bondable>> generateBondGraph() {
        Map<Bondable, Set<Bondable>> bondables = new WeakHashMap<>();
        bonds.forEach(b -> {
            bondables.computeIfAbsent(b.getStartBondable(), bd -> Collections.newSetFromMap(new WeakHashMap<>())).add(b.getEndBondable());
            bondables.computeIfAbsent(b.getEndBondable(), bd -> Collections.newSetFromMap(new WeakHashMap<>())).add(b.getStartBondable());
        });
        return bondables;
    }

    @Override
    public XMLData serialize() {
        XMLDataBuilder builder = XMLDataBuilder.newInstance().setElement("Model").setData(id);
        Stream.concat(rGroups.stream(), bonds.stream())
                .map(XMLSerializable::serialize).forEach(builder::addNestedXMLData);
        return builder.build();
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    public static Model deserialize(XMLData data) {
        if (!"Model".equals(data.element())) {
            throw new IllegalArgumentException("Not a model!!!");
        }
        Map<String, Bondable> bondables = new HashMap<>();
        Model model = Model.create();
        data.nested().forEach(d -> {
            switch (d.element()) {
                case "Atom":
                    Atom a = Atom.deserialize(d);
                    model.add(a);
                    bondables.put(a.getId(), a);
                    break;
                case "RGroup":
                    RGroup r = RGroup.deserialize(d);
                    model.add(r);
                    bondables.put(r.getId(), r);
                    break;
                case "Bond":
                    Bond b = BondImpl.deserialize(d, bondables::get);
                    model.add(b);
                    break;
            }
        });
        return model;
    }

    @Override
    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
    public boolean equals(Object other) {
        return XMLSerializable.equals(this, other);
    }

    @Override
    public int hashCode() {
        return XMLSerializable.hashCode(this);
    }

    public void validateId(Identifiable id) {
        int tagIndex = id.getId().indexOf('$');
        id.setId(idLogger.forceAdd(id.getId().substring(0, tagIndex < 0 ? id.getId().length() : tagIndex)));
    }
}
