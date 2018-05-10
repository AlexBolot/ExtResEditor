package fr.unice.polytech.si3.qgl.ise.service;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/*................................................................................................................................
 . Copyright (c)
 .
 . The ArrayList8	 Class was Coded by : Alexandre BOLOT
 .
 . Last Modified : 16/03/18 15:19
 .
 . Contact : bolotalex06@gmail.com
 ...............................................................................................................................*/

@SuppressWarnings("WeakerAccess")
public class ArrayList8<E> extends ArrayList<E>
{
    //region --------------- Constructors --------------------
    public ArrayList8 () { super(); }

    public ArrayList8 (@NotNull Collection<? extends E> c) { super(c); }
    //endregion

    //region --------------- Methods -------------------------

    public boolean addIf (E value, @NotNull Predicate<? super E> filter)
    {
        return filter.test(value) && add(value);
    }

    @NotNull
    public ArrayList8<E> subList (@NotNull Predicate<? super E> filter)
    {
        ArrayList8<E> res = new ArrayList8<>();

        this.forEach(e -> res.addIf(e, filter));

        return res;
    }

    public <R> ArrayList8<R> mapAndCollect (@NotNull Function<? super E, ? extends R> mapper)
    {
        return this.stream().map(mapper).collect(Collectors.toCollection(ArrayList8::new));
    }
    //endregion
}
