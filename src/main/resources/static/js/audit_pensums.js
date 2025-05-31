document.addEventListener("DOMContentLoaded", () => {
  cargarPensums();
});

const token = localStorage.getItem("token");

function cargarPensums() {
  fetch("/api/pensums", {
      headers: {
        "Authorization": `Bearer ${token}`
      }
    })
    .then(res => res.json())
    .then(data => {
      const container = document.querySelector(".pensums");
      data.forEach(pensum => container.appendChild(CrearElementoPensum(pensum)));
    })
    .catch(err => console.error("Error cargando Pensums:", err));
}

function CrearElementoPensum(pensum) {
  const div = document.createElement("div");
  div.className = "pensum_item";

  Object.assign(div.style, {
    display: "flex",
    justifyContent: "space-between",
    width: "100%",
    cursor: "pointer",
    position: "relative"
  });

  div.innerHTML = `
    <p>${pensum.codigo}</p>
    <p>${pensum.activo}</p>
    <div class="pensum-actions">
      <span class="informe-icon">
        <i class="fas fa-cog"></i>
      </span>
    </div>
  `;

  div.addEventListener("click", e => {
    if (!e.target.classList.contains("informe-icon")) {
      abrirModalConMaterias(pensum.codigo);
    }
  });

  return div;
}

function abrirModalConMaterias(codigoPensum) {
  fetch(`/api/pensums/${codigoPensum}/materias`, {
    headers: { "Authorization": `Bearer ${token}` }
  })
  .then(res => res.json())
  .then(data => {
    const materiasPorSemestre = agruparMateriasPorSemestre(data);
    const contenedor = document.getElementById("materiasPorSemestre");
    contenedor.innerHTML = "";

    for (const semestre in materiasPorSemestre) {
      const bloque = document.createElement("div");
      bloque.className = "semestre-bloque";

      bloque.innerHTML = `
        <h3>Semestre ${semestre}</h3>
        <table>
          <thead>
            <tr>
              <th>Código</th>
              <th>Nombre</th>
              <th>Créditos</th>
              <th>Tipo</th>
              <th>Prerrequisitos</th>
              <th>Descripción</th>
            </tr>
          </thead>
          <tbody>
            ${materiasPorSemestre[semestre].map(m => `
              <tr>
                <td>${m.codigo}</td>
                <td>${m.nombre}</td>
                <td>${m.creditos}</td>
                <td>${m.tipo}</td>
                <td>${m.prerequisitos ? m.prerequisitos.join(', ') : '-'}</td>
                <td>${m.descripcion}</td>
              </tr>
            `).join('')}
          </tbody>
        </table>
      `;

      contenedor.appendChild(bloque);
    }

    document.getElementById("pensumModal").style.display = "flex";
  })
  .catch(err => {
    console.error("Error al cargar materias del pensum", err);
    Swal.fire("Error", "No se pudo cargar la información del pensum.", "error");
  });
}

function agruparMateriasPorSemestre(materias) {
  const agrupadas = {};
  materias.forEach(m => {
    const semestre = m.semestre || 1;
    if (!agrupadas[semestre]) agrupadas[semestre] = [];
    agrupadas[semestre].push(m);
  });

  // Ordenar por semestre numérico ascendente
  const ordenadas = {};
  Object.keys(agrupadas).sort((a, b) => a - b).forEach(s => {
    ordenadas[s] = agrupadas[s];
  });

  return ordenadas;
}

// Cerrar modal al hacer clic fuera o en la "x"
document.addEventListener("DOMContentLoaded", () => {
  const modal = document.getElementById("pensumModal");
  const closeBtn = document.getElementById("closeModal");

  closeBtn.addEventListener("click", () => modal.style.display = "none");

  window.addEventListener("click", e => {
    if (e.target === modal) {
      modal.style.display = "none";
    }
  });
});
