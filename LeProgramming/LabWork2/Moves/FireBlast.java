package Moves;

import ru.ifmo.se.pokemon.*;

public class FireBlast extends SpecialMove
{
    public FireBlast()
    {
        super(Type.FIRE, 100, 85);
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
        return "uses fireBlast";
    }
}
