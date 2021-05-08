package app.tests.simulation;

interface Pool<T> {
    void addItem(T item, Integer prob);
    T draw();
}
