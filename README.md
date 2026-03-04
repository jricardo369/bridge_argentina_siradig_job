# BSM_SIRADIG_ARG

Aplicación Java para procesamiento de información SIRADIG (ARG), carga a base de datos Oracle y ejecución de envío de correos.

## Qué hace este proyecto

- Lee archivos XML de SIRADIG desde rutas configuradas en el código.
- Procesa y transforma datos de empleados, ganancias, deducciones, retenciones y otros conceptos.
- Inserta información en tablas Oracle (`BSM_SIRADIG_ARG_*`).
- Soporta lectura/procesamiento de datos auxiliares en PDF/JSON (según flujo interno).
- Ejecuta envío de correos en modo proceso.

Clase principal:

- `BSMSIRADIGARG`

Paquetes principales:

- `src/helper`
- `src/util`
- `src/vo`

## Requisitos

- JDK 8 (o superior con `source/target` 1.8)
- Maven 3.6+
- Acceso a base de datos Oracle
- Rutas/configuración de entorno esperadas por la aplicación (por ejemplo archivos `.properties` y rutas de entrada)

## Compilación

Desde la raíz del proyecto:

```bash
mvn -q -DskipTests package
```

Esto genera:

- `target/BSM_SIRADIG_ARG.jar`
- `dist/BSM_SIRADIG_ARG.jar`

El JAR es autocontenido (fat jar) y no requiere carpeta `lib` externa.

## Ejecución

Ejemplo de ejecución (modo prueba):

```bash
java -jar dist/BSM_SIRADIG_ARG.jar 1
```

Parámetros esperados por `main`:

- `0`: modo proceso (envío de correos)
- `1`: modo prueba

> Nota: si faltan rutas o archivos de configuración del entorno (por ejemplo `.properties` o rutas de red/disco), la aplicación puede iniciar pero fallar durante la carga o conexión.

## Limpieza y rebuild

```bash
mvn clean package -DskipTests
```

