package Moves;

import ru.ifmo.se.pokemon.*;

public class Scald extends SpecialMove {
    public Scald()
    {
        super(Type.WATER, 80, 100);
    }

    @Override
    protected void applyOppEffects(Pokemon enemyPokemon)
    {
        if(Math.random() < 0.1)
        {
            Effect.burn(enemyPokemon);
        }
    }

    @Override
    protected String describe()
    {
        return "uses Scald";
    }
}