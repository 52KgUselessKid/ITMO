package Moves;

import ru.ifmo.se.pokemon.*;

public class AquaTail extends PhysicalMove {
    public AquaTail()
    {
        super(Type.WATER, 90, 90);
    }

    @Override
    protected String describe()
    {
        return "uses AquaTail";
    }
}