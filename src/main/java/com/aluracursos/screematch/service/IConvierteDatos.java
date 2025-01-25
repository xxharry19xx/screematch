package com.aluracursos.screematch.service;

public interface IConvierteDatos {

    <T> T obetenerDatos(String json, Class<T> clase);
}
