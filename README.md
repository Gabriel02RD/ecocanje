# 🌱 EcoCanje

**EcoCanje** es una aplicación de escritorio desarrollada en **Java Swing** que incentiva 
el reciclaje mediante un sistema de recompensas: los usuarios acumulan puntos al 
depositar materiales reciclables y los canjean por productos de canasta familiar.

## ✨ Características

- 👤 Registro e inicio de sesión de usuarios (con validación de correo y cédula).
- ♻️ Registro de reciclaje por tipo de material y peso (puntos automáticos).
- 🛒 Catálogo de productos canjeables con control de stock.
- 📊 Panel de inicio con estadísticas en tiempo real.
- 💾 Persistencia automática de datos (serialización Java a `ecocanje_data.ser`).
- 🎨 Interfaz moderna con paneles con degradado, botones redondeados y paleta ecológica.
- 📱 Sistema compatible con tarjetas QR / recipientes IoT (visión del proyecto).

## 🛠️ Tecnologías

- **Java 23** (JDK 23 requerido)
- **Java Swing** para la interfaz gráfica
- **Serialización Java** para la persistencia de datos
- **Apache Ant** (proyecto NetBeans) como sistema de construcción

## 🚀 Cómo ejecutar

```bash
# Compilar
ant compile

# Ejecutar
ant run

# Construir JAR
ant jar
# Ejecutar el JAR
java -jar dist/EcoCanje.jar
