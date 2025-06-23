# Especificación Técnica - Librería

## Descripción General
Aplicación Android para la gestión de una biblioteca personal. Permite escanear libros por ISBN, consultar detalles, editar información, fijar ubicación por defecto, gestionar una wishlist y exportar la base de datos por email en CSV.

## Estructura de Datos

### Modelo Book
```kotlin
@Entity(tableName = "books")
data class Book(
    @PrimaryKey val isbn: String,
    val title: String,
    val author: String,
    val coverUrl: String?,
    val price: Double?,
    val review: String?,
    val synopsis: String?,
    val bookcaseNumber: Int?,
    val shelfNumber: Int?,
    val editorial: String?,
    val pageCount: Int?,
    val addedDate: Long = System.currentTimeMillis()
)
```

### Modelo WishlistBook
```kotlin
@Entity(tableName = "wishlist")
data class WishlistBook(
    @PrimaryKey val isbn: String,
    val title: String,
    val author: String,
    val coverUrl: String?,
    val price: Double?,
    val editorial: String?,
    val pageCount: Int?,
    val addedDate: Long = System.currentTimeMillis()
)
```

## Persistencia
- **Room**: Almacena los libros y la wishlist localmente.
- **SharedPreferences**: Guarda la ubicación por defecto (estantería y repisa).

## Funcionalidad Clave
- **Escaneo de libros**: Permite añadir libros a la biblioteca o a la wishlist tras escanear el ISBN. Si el libro ya está en la biblioteca, muestra un mensaje. Si está en la wishlist, permite moverlo a la biblioteca.
- **Filtro visual**: En la pantalla principal se puede alternar entre ver todos los libros, solo la biblioteca o solo la wishlist. Los libros de la wishlist se distinguen con un icono.
- **Detalle y edición**: Las pantallas de detalle y edición tienen scroll vertical y muestran todos los campos, aunque sean largos.
- **Exportar CSV**: Se puede exportar la base de datos de libros a CSV y enviarla por email mediante un icono en la barra superior.

## Lógica de Ubicación por Defecto
- Al crear un nuevo libro, si no se especifica ubicación, se asigna la ubicación por defecto a los campos `bookcaseNumber` y `shelfNumber`.
- La ubicación por defecto se puede fijar desde el menú principal.
- En la ficha de detalle y edición, la ubicación es editable y se muestra siempre como valor propio del libro.

## Lógica de Precio
- El precio se obtiene del API (si está disponible) y se almacena en el campo `price`.
- El precio es editable desde la ficha de edición.
- En la ficha de detalle se muestra siempre el precio (o "No disponible").

## Pantallas Principales
- **LibraryScreen**: Lista de libros, filtro visual, exportar CSV.
- **BookDetailScreen**: Ficha de detalle (muestra todos los campos, permite editar y borrar).
- **EditBookScreen**: Permite editar todos los campos, incluyendo ubicación y precio.
- **ScanScreen**: Escaneo de ISBN y alta rápida de libros, opción de añadir a biblioteca o wishlist.

## Llamadas al API
- **Retrofit** se usa para consultar la API de Google Books:
    - Buscar libro por ISBN: `GET https://www.googleapis.com/books/v1/volumes?q=isbn:{isbn}`
    - Se extraen: título, autor, editorial, páginas, sinopsis, carátula, precio (si está disponible).

## Flujo de Alta de Libro
1. El usuario escanea un ISBN.
2. Se consulta la API de Google Books.
3. Si el libro no existe en la base local ni en la wishlist, se muestra un diálogo para elegir si añadir a biblioteca o wishlist.
4. Si está en la wishlist, se ofrece moverlo a la biblioteca.
5. Si ya está en la biblioteca, se muestra un mensaje.

## Edición y Detalle
- En la ficha de detalle y edición se pueden modificar todos los campos relevantes, incluyendo ubicación y precio.

## Exportación
- Permite exportar la base de datos de libros a CSV y compartirla por email mediante un icono.

## Sincronización con Servidor Local (FastAPI)

La aplicación puede sincronizar su base de datos de libros y wishlist con un servidor Python en la red local. El servidor expone endpoints REST para:
- Obtener todos los libros y wishlist
- Añadir, actualizar y eliminar libros y wishlist
- Sincronizar cambios entre varios dispositivos

### Requisitos del servidor
- Implementado en Python usando FastAPI
- Accesible en la red local (por ejemplo, http://192.168.1.100:8000)
- Almacena los datos en una base de datos SQLite compatible con los modelos de la app
- Proporciona endpoints CRUD para ambos modelos

### Prompt para generación automática del servidor (ChatGPT/Copilot):

> Crea un servidor Python usando FastAPI que implemente los siguientes modelos y exponga endpoints REST para CRUD y sincronización. Usa SQLite como base de datos. El servidor debe ser accesible en la red local y permitir la sincronización de datos con una app Android. Incluye endpoints para obtener, crear, actualizar y eliminar tanto libros como wishlist. Los modelos son:
>
> ```python
> from pydantic import BaseModel
> from typing import Optional
> import datetime
>
> class Book(BaseModel):
>     isbn: str
>     title: str
>     author: str
>     coverUrl: Optional[str]
>     price: Optional[float]
>     review: Optional[str]
>     synopsis: Optional[str]
>     bookcaseNumber: Optional[int]
>     shelfNumber: Optional[int]
>     editorial: Optional[str]
>     pageCount: Optional[int]
>     addedDate: Optional[int]  # timestamp
>
> class WishlistBook(BaseModel):
>     isbn: str
>     title: str
>     author: str
>     coverUrl: Optional[str]
>     price: Optional[float]
>     editorial: Optional[str]
>     pageCount: Optional[int]
>     addedDate: Optional[int]  # timestamp
> ```

## Dependencias Clave
- Jetpack Compose (UI)
- Room (persistencia local)
- Retrofit + Gson (API REST)
- Hilt (inyección de dependencias)
- CameraX y MLKit (escaneo de códigos)

---

*Última actualización: 2025-06-23*
