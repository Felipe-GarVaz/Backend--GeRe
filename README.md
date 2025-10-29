🧭 GEVIDI - Sistema de Gestión Vehicular Distribuido

Aplicación web full stack para la gestión vehicular y control de información interna.
Desarrollada con Spring Boot (Java), React (JavaScript) y MySQL, y desplegada mediante Docker Compose.

🧱 Arquitectura del Proyecto
/GEVIDI
│
├── backend/                # Backend en Spring Boot
│   ├── pom.xml
│   └── src/
│
├── frontend/               # Frontend en React.js
│   ├── package.json
│   └── src/
│
├── gevidi.sql              # Script de inicialización de base de datos
├── Dockerfile              # Build unificado (backend + frontend)
├── docker-compose.yml      # Orquestación de contenedores
└── README.md               # Este archivo

⚙️ Tecnologías utilizadas
Componente                  Tecnología
Backend                     Java 21, Spring Boot 3.5.3, Maven
Frontend                    React 18, Node.js 20, npm
Base de Datos               MySQL 8.0
Contenedores                Docker & Docker Compose
Seguridad                   JWT, Spring Security

🚀 Despliegue local (Docker)

✅ Requisitos previos

Asegúrate de tener instalado:
	•	Docker
	•	Docker Compose

Verifica las versiones:
docker --version
docker compose version

▶️ Pasos para levantar el entorno
1.	Clonar o copiar el proyecto en el servidor o equipo local.
git clone https://<tu-repositorio>/GEVIDI.git
cd GEVIDI

2.	Construir e iniciar los servicios
docker-compose up --build -d

3.	Verificar que todo esté corriendo
docker ps

CONTAINER ID   NAME             STATUS           PORTS
a1b2c3d4e5f6   gevidi-db        healthy          0.0.0.0:3306->3306/tcp
b2c3d4e5f6a7   gevidi-backend   Up (healthy)     0.0.0.0:8080->8080/tcp

4.	Abrir la aplicación
	•	🌐 Aplicación Web: http://localhost:8080
	•	🛢️ Base de datos MySQL:
	•	Host: localhost
	•	Puerto: 3306
	•	Usuario: gevidi_user
	•	Contraseña: gevidi_pass
	•	Base de datos: gevidi

🧩 Variables de entorno

Puedes mover las variables a un archivo .env si lo deseas:

📄 .env
MYSQL_ROOT_PASSWORD=root
MYSQL_DATABASE=gevidi
MYSQL_USER=gevidi_user
MYSQL_PASSWORD=gevidi_pass
SPRING_DATASOURCE_URL=jdbc:mysql://db:3306/gevidi?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
SPRING_DATASOURCE_USERNAME=gevidi_user
SPRING_DATASOURCE_PASSWORD=gevidi_pass
SPRING_JPA_HIBERNATE_DDL_AUTO=update
SPRING_JPA_PROPERTIES_HIBERNATE_DIALECT=org.hibernate.dialect.MySQLDialect

Luego en docker-compose.yml, reemplaza los valores por:
env_file:
  - .env

🔧 Comandos útiles
Acción                                 Comando
Levantar todo                          docker-compose up --build -d
Detener servicios                      docker-compose down
Ver logs en vivo                       docker-compose logs -f
Reiniciar el backend                   docker-compose restart backend
Borrar base de datos (reinicializar)   docker-compose down -v && docker-compose up --build

🧠 Estructura de Red y Puertos
Servicio            Puerto interno          Puerto host         Descripción
Backend                  8080                   8080           API + Frontend
MySQL                    3306                   3306           Base de datos


🧾 Licencia

Proyecto desarrollado como parte de GEVIDI.
Uso interno o académico. No distribuir sin autorización.
