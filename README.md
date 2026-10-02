# PsicoGestión - Clínica SEAP UADY

## Descripción del Proyecto
Diseñado para soportar la gestión operativa de citas de seguimiento, la validación estricta de la disponibilidad de salas y terapeutas, y la prevención de colisiones de horarios. Este sistema está dirigido a la Clínica de Asuntos Generales del SEAP de la Universidad Autónoma de Yucatán (UADY).

## Arquitectura
El desarrollo del sistema se rige por el patrón de **Clean Architecture** (Arquitectura Limpia) y el cumplimiento estricto de los principios SOLID. El sistema se divide en las siguientes capas lógicas:
* **Capa de Entidades:** Contiene las reglas de negocio empresariales y los objetos de dominio puros.
* **Capa de Casos de Uso:** Orquesta el flujo de datos y aplica la lógica operativa de la clínica (Módulos de Cuentas, Catálogos y Core de Agenda).
* **Capa de Puertos e Infraestructura:** Define las interfaces de adaptadores (repositorios)[cite: 8] e implementa la persistencia de datos y servicios externos.

## Stack Tecnológico
* **Frontend:** Interfaz de usuario responsiva construida con **React** y empaquetada con **Vite**.
* **Backend:** API REST y Lógica de Negocio implementada en **Java con Spring Boot**. Proporciona un tipado estático fuerte y manejo nativo de interfaces para la Inversión de Dependencias.
* **Base de Datos:** Motor Relacional **MySQL (InnoDB)**. Utilizado para soportar transacciones ACID y bloqueos pesimistas, garantizando la consistencia e integridad de la agenda médica frente a la concurrencia.

## Estructura del Repositorio
El repositorio está organizado físicamente para garantizar la separación de responsabilidades:
* `/docs/`: Documentación del proyecto (DAS, ERS, Manuales).
* `/frontend/`: Aplicación web.
* `/backend/`: API y Lógica de negocio.
* `/database/`: Scripts SQL (DDL y DML) para MySQL.

## Instalación y Configuración Local
### Pre-requisitos
* Node.js y npm instalados.
* Java Development Kit (JDK) 17.
* Maven.
* MySQL Server ejecutándose localmente.

### Frontend
1. Navegar a la carpeta del frontend: `cd frontend`
2. Instalar dependencias: `npm install`
3. Levantar el entorno de desarrollo: `npm run dev`

### Backend
1. Navegar a la carpeta del backend: `cd backend`
2. Ejecutar la aplicación con Maven: `./mvnw spring-boot:run`

## Equipo de Desarrollo
**Linea Base 2000**
**Administración de Proyectos II - Equipo 2**:
* Aguilar Pérez Johan Ricardo
* Calderón Núñez Mariano Marcel
* Ceballos Pérez Andrea
* González Canul Mariana Estefania
* Kuh Esquivel Mauro Arif
