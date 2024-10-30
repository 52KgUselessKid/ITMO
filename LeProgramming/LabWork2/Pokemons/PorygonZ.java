package Pokemons;

import Moves.*;
import ru.ifmo.se.pokemon.*;

public class PorygonZ extends Pokemon {
    public PorygonZ(String name, int level)
    {
        super(name, level);
        setType(Type.NORMAL);
        setStats(85, 80, 70, 135, 75, 90);

        Thunderbolt thunderbolt = new Thunderbolt();
        Confide confide = new Confide();
        DefenseCurl defenseCurl = new DefenseCurl();

        setMove(thunderbolt, confide, defenseCurl, confide);
    }

}