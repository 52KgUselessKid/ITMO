package Pokemons;

import Moves.*;
import ru.ifmo.se.pokemon.*;

public class Porygon2 extends Pokemon {
    public Porygon2(String name, int level)
    {
        super(name, level);
        setType(Type.NORMAL);
        setStats(85, 80, 90, 105, 95, 60);

        Thunderbolt thunderbolt = new Thunderbolt();
        Confide confide = new Confide();
        DefenseCurl defenseCurl = new DefenseCurl();

        setMove(thunderbolt, confide, defenseCurl);
    }

}