package ptml.releasing.app.db.mapper

interface DbMapper<C, E> {

    fun mapFromCached(type: C): E

    fun mapToCached(type: E): C

}