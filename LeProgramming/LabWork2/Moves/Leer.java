package Moves;

import ru.ifmo.se.pokemon.*;

public class Leer extends StatusMove
{
    public Leer()
    {
        super(Type.NORMAL,0,100);
    }

    @Override
    protected void applyOppEffects(Pokemon enemyPokemon)
    {
        Effect e = new Effect().stat(Stat.DEFENSE, -1);
        enemyPokemon.addEffect(e);
    }

    @Override
    protected String describe()
    {
        return "uses Leer";
    }
}