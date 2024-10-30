package Moves;

import ru.ifmo.se.pokemon.*;

public class Thunderbolt extends SpecialMove {
    public Thunderbolt()
    {
        super(Type.ELECTRIC, 90, 100);
    }

    @Override
    protected void applyOppEffects(Pokemon enemyPokemon)
    {
        if(Math.random() < 0.1)
        {
            Effect.paralyze(enemyPokemon);
        }
    }

    @Override
    protected String describe()
    {
        return "uses Scald";
    }
}