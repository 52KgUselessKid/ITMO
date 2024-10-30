package Moves;

import ru.ifmo.se.pokemon.*;

public class Stomp extends  PhysicalMove {
    public Stomp()
    {
        super(Type.NORMAL, 65, 100);
    }

    @Override
    protected void applyOppEffects(Pokemon enemyPokemon)
    {
        if(Math.random() < 0.3)
        {
            Effect.flinch(enemyPokemon);
        }
    }

    @Override
    protected String describe()
    {
        return "uses Stomp";
    }
}