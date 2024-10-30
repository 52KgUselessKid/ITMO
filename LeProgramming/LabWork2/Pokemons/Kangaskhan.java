package Pokemons;

import Moves.*;
import ru.ifmo.se.pokemon.*;

public class Kangaskhan extends Pokemon {
    public Kangaskhan(String name, int level)
    {
        super(name, level);
        setType(Type.NORMAL);
        setStats(105, 95, 80, 40, 80, 90);

        FireBlast fireBlast = new FireBlast();
        Confide confide = new Confide();
        Leer leer = new Leer();
        Stomp stomp = new Stomp();

        setMove(fireBlast, confide, leer, stomp);
    }

}