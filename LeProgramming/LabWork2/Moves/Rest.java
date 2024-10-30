package Moves;

import ru.ifmo.se.pokemon.*;

public class Rest extends StatusMove {
    public Rest()
    {
        super(Type.PSYCHIC, 0, 0);
    }

    @Override
    protected void applySelfEffects(Pokemon selfPokemon)
    {
        selfPokemon.restore();
        Effect e = new Effect().turns(2).condition(Status.SLEEP);
        selfPokemon.addEffect(e);
    }

    @Override
    protected String describe()
    {
        return "uses Rest";
    }
}