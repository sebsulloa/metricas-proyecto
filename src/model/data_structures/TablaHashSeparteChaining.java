package model.data_structures;

public class TablaHashSeparteChaining<K extends Comparable<K>, V extends Comparable<V>> extends TablaHash<K,V> {
    
    private ILista<ILista<NodoTS<K,V>>> listaNodos;

    public TablaHashSeparteChaining(int tamInicial) {
        super(tamInicial);
        listaNodos = new ArregloDinamico<>(tamanoTabla);
        
        for(int i=1; i<=tamanoTabla; i++) {
            try {
                listaNodos.insertElement(null, i);
            } catch (DataStructureException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void put(K key, V value) {
        int posicion = hash(key);
        try {
            ILista<NodoTS<K,V>> listasc = listaNodos.getElement(posicion);
            
            if(listasc != null && !contains(key)) {
                listasc.insertElement(new NodoTS<K,V>(key, value), listasc.size()+1);
            }
            else {
                listaNodos.changeInfo(posicion, new ArregloDinamico<NodoTS<K,V>>(1));
                listasc = listaNodos.getElement(posicion);
                listasc.insertElement(new NodoTS<K,V>(key, value), listasc.size()+1);
            }
        } catch (DataStructureException e) {
            e.printStackTrace();
        }
        
        tamanoAct++;
        
        double factorCarga = (double)tamanoAct/tamanoTabla;
        if (factorCarga > 5) {
            rehash();
        }
    }

    @Override
    public V get(K key) {
        V retornar = null;
        int posicion = hash(key);
        try {
            ILista<NodoTS<K,V>> listasc = listaNodos.getElement(posicion);
            if(listasc != null) {
                for(int i=1; i<=listasc.size() && retornar==null; i++) {
                    if(listasc.getElement(i).getKey().compareTo(key) == 0) {
                        retornar = listasc.getElement(i).getValue();
                    }
                }
            }
        } catch (DataStructureException e) {
            e.printStackTrace();
        }
        return retornar;
    }

    @Override
    public V remove(K key) {
        V retornar = null;
        int posicion = hash(key);
        try {
            ILista<NodoTS<K,V>> listasc = listaNodos.getElement(posicion);
            if(listasc != null) {
                for(int i=1; i<=listasc.size() && retornar==null; i++) {
                    if(listasc.getElement(i).getKey().compareTo(key) == 0) {
                        retornar = listasc.getElement(i).getValue();
                        listasc.deleteElement(i);
                    }
                }
            }
        } catch (DataStructureException e) {
            e.printStackTrace();
        }
        if(retornar != null) {
            tamanoAct--;
        }
        return retornar;
    }

    @Override
    public boolean contains(K key) {
        return get(key) != null;
    }

    @Override
    public ILista<K> keySet() {
        ILista<K> lista = new ArregloDinamico<>(1);
        try {
            for (int i=1; i <= tamanoTabla; i++) {
                ILista<NodoTS<K,V>> listacad = listaNodos.getElement(i);
                if (listacad != null) {
                    for (int j=1; j <= listacad.size(); j++) {
                        lista.insertElement(listacad.getElement(j).getKey(), lista.size()+1);
                    }
                }
            }
        }
        catch (DataStructureException e) {
            e.printStackTrace();
        }
        return lista;
    }

    @Override
    public ILista<V> valueSet() {
        ILista<V> lista = new ArregloDinamico<>(1);
        try {
            for (int i=1; i <= tamanoTabla; i++) {
                ILista<NodoTS<K,V>> listacad = listaNodos.getElement(i);
                if (listacad != null) {
                    for (int j=1; j <= listacad.size(); j++) {
                        lista.insertElement(listacad.getElement(j).getValue(), lista.size()+1);
                    }
                }
            }
        }
        catch (DataStructureException e) {
            e.printStackTrace();
        }
        return lista;
    }

    @Override
    public ILista<NodoTS<K,V>> darListaNodos() {
        ILista<NodoTS<K,V>> nodos = new ArregloDinamico<>(1);
        try {
            for (int i=1; i <= tamanoTabla; i++) {
                ILista<NodoTS<K,V>> listacad = listaNodos.getElement(i);
                if(listacad != null && !listacad.isEmpty()) {
                    for(int j=1; j <= listacad.size(); j++) {
                        NodoTS<K,V> nodo = listacad.getElement(j);
                        if(nodo != null && !listacad.isEmpty()) {
                            nodos.insertElement(nodo, nodos.size()+1);
                        }
                    }
                }
            }
        }
        catch (DataStructureException e) {
            e.printStackTrace();
        }
        return nodos;
    }

    private void rehash() {
        try {
            ILista<NodoTS<K,V>> nodos = darListaNodos();
            
            tamanoAct = 0;
            tamanoTabla *= 2;
            tamanoTabla = nextPrime(tamanoTabla);
            listaNodos = new ArregloDinamico<>(tamanoTabla);
            
            for(int i=1; i<=tamanoTabla; i++) {
                listaNodos.insertElement(null, i);
            }
            
            for(int i=1; i <= nodos.size(); i++) {
                NodoTS<K,V> actual = nodos.getElement(i);
                put(actual.getKey(), actual.getValue());
            }
            cantidadRehash++;
        }
        catch (DataStructureException e) {
            e.printStackTrace();
        }
    }
}