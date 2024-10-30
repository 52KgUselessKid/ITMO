package Moves;

import ru.ifmo.se.pokemon.*;

public class DefenseCurl extends StatusMove {
    public DefenseCurl()
    {
        super(Type.NORMAL, 0, 0);
    }

    @Override
    protected void applySelfEffects(Pokemon selfPokemon)
    {
        selfPokemon.restore();
        Effect e = new Effect().stat(Stat.DEFENSE, 1);
        selfPokemon.addEffect(e);
    }

    @Override
    protected String describe()
    {
        return "uses DefenseCurl";
    }
}