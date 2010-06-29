package com.foo;

@SuppressWarnings("all")
public class StubDomainTypeBase implements DomainType {

    protected String name;
    protected Integer id;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

}
