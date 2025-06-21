# Especificación Técnica - Librería

## Descripción General
Aplicación Android para la gestión de una biblioteca personal. Permite escanear libros por ISBN, consultar detalles, editar información, fijar ubicación por defecto y almacenar los datos localmente.

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

## Persistencia
- **Room**: Almacena los libros localmente.
- **SharedPreferences**: Guarda la ubicación por defecto (estantería y repisa).

## Lógica de Ubicación por Defecto
- Al crear un nuevo libro, si no se especifica ubicación, se asigna la ubicación por defecto a los campos `bookcaseNumber` y `shelfNumber`.
- La ubicación por defecto se puede fijar desde el menú principal.
- En la ficha de detalle y edición, la ubicación es editable y se muestra siempre como valor propio del libro.

## Lógica de Precio
- El precio se obtiene del API (si está disponible) y se almacena en el campo `price`.
- El precio es editable desde la ficha de edición.
- En la ficha de detalle se muestra siempre el precio (o "No disponible").

## Pantallas Principales
- **LibraryScreen**: Lista de libros.
- **BookDetailScreen**: Ficha de detalle (muestra todos los campos, permite editar y borrar).
- **EditBookScreen**: Permite editar todos los campos, incluyendo ubicación y precio.
- **ScanScreen**: Escaneo de ISBN y alta rápida de libros.

## Llamadas al API
- **Retrofit** se usa para consultar la API de Google Books:
    - Buscar libro por ISBN: `GET https://www.googleapis.com/books/v1/volumes?q=isbn:{isbn}`
    - Se extraen: título, autor, editorial, páginas, sinopsis, carátula, precio (si está disponible).

## Flujo de Alta de Libro
1. El usuario escanea un ISBN.
2. Se consulta la API de Google Books.
3. Si el libro no existe en la base local, se muestra un diálogo de confirmación.
4. Al confirmar, se crea el libro con los datos obtenidos y la ubicación por defecto (si no se especifica otra).

## Edición y Detalle
- En la ficha de detalle y edición se pueden modificar todos los campos relevantes, incluyendo ubicación y precio.

## Dependencias Clave
- Jetpack Compose (UI)
- Room (persistencia local)
- Retrofit + Gson (API REST)
- Hilt (inyección de dependencias)
- CameraX y MLKit (escaneo de códigos)

---

*Última actualización: 2025-06-21*
