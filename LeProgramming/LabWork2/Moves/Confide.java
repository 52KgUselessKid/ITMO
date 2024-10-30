package Moves;

import ru.ifmo.se.pokemon.*;

public class Confide extends StatusMove
{
    public Confide()
    {
        super(Type.NORMAL,0,0);
    }

    @Override
    protected void applyOppEffects(Pokemon enemyPokemon)
    {
        Effect e = new Effect().stat(Stat.SPECIAL_ATTACK, -1);
        enemyPokemon.addEffect(e);
    }

    @Override
    protected String describe()
    {
        return "uses Confide";
    }
}