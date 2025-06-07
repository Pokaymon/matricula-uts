const token = localStorage.getItem("token");

document.addEventListener("DOMContentLoaded", () => {
  cargarEstudiantes();
});

async function cargarEstudiantes() {
  try {
    const response = await fetch("/api/users/estudiantes", {
      headers: {
        "Authorization": `Bearer ${token}`,
        "Content-Type": "application/json"
      }
    });

    if (!response.ok) {
      throw new Error(`Error ${response.status}: ${response.statusText}`);
    }

    const estudiantes = await response.json();

    if (!Array.isArray(estudiantes)) {
      throw new Error("Respuesta no válida del servidor");
    }

    mostrarEstudiantes(estudiantes);
  } catch (error) {
    console.error("Error cargando estudiantes:", error);
    alert("No se pudieron cargar los estudiantes. ¿Estás autenticado?");
  }
}

function mostrarEstudiantes(estudiantes) {
  const container = document.querySelector(".groups");
  container.innerHTML = ""; // Limpiar antes de agregar

  estudiantes.forEach(estudiante => {
    const element = crearElementoEstudiante(estudiante);
    container.appendChild(element);
  });
}

function crearElementoEstudiante(estudiante) {
  const div = document.createElement("div");
  div.className = "group_item";

  // Aquí mostramos solo cedula, rol, y nombre + apellido, respetando las clases CSS que uses.
  div.innerHTML = `
    <p class="cedula">${estudiante.cedula}</p>
    <p class="rol">${estudiante.rol}</p>
    <p class="nombre">${estudiante.nombre} ${estudiante.apellido}</p>
  `;

  // Si necesitas agregar eventos (como abrir detalles), lo agregas aquí.
  // Por ejemplo:
  // div.addEventListener("click", () => abrirDetalles(estudiante.id));

  return div;
}

