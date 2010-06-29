package com.foo;

@SuppressWarnings("all")
public class StubDomainTypeBase implements DomainType {

    protected String _name;
    protected Integer _id;

    public String getName() {
        return _name;
    }

    public void setName(String _name) {
        this._name = _name;
    }

    public Integer getId() {
        return _id;
    }

}
