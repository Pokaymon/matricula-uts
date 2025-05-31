document.addEventListener("DOMContentLoaded", () => {
  const token = localStorage.getItem("token");

  cargarPensums(token);
  inicializarModal();
});

// Cargar Pensums (solo inactivos)
function cargarPensums(token) {
  fetch("/api/pensums", {
    headers: {
      "Authorization": `Bearer ${token}`
    }
  })
    .then(res => res.json())
    .then(data => {
      const container = document.querySelector(".pensums");
      container.innerHTML = "";

      data
        .filter(pensum => !pensum.activo)
        .forEach(pensum => container.appendChild(CrearElementoPensum(pensum, token)));
    })
    .catch(err => console.error("Error cargando Pensums:", err));
}

// Crear el elemento visual para cada pensum
function CrearElementoPensum(pensum, token) {
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
    <p>${pensum.activo ? "Activo" : "Pendiente Por Activación"}</p>
    <div class="pensum-actions">
      <span class="informe-icon">
        <i class="fas fa-puzzle-piece"></i>
      </span>
    </div>
  `;

  div.addEventListener("click", e => {
    if (!e.target.closest(".informe-icon")) {
      abrirModalConMaterias(pensum.codigo, token);
    }
  });

  return div;
}

// Abrir modal con las materias agrupadas
function abrirModalConMaterias(codigoPensum, token) {
  fetch(`/api/pensums/${codigoPensum}/materias`, {
    headers: { "Authorization": `Bearer ${token}` }
  })
    .then(res => res.json())
    .then(data => {
      const materiasPorSemestre = agruparMateriasPorSemestre(data);
      renderizarMateriasPorSemestre(materiasPorSemestre);
      document.getElementById("pensumModal").style.display = "flex";
    })
    .catch(err => {
      console.error("Error al cargar materias del pensum", err);
      Swal.fire("Error", "No se pudo cargar la información del pensum.", "error");
    });
}

// Renderizar materias dentro del modal
function renderizarMateriasPorSemestre(materiasPorSemestre) {
  const contenedor = document.getElementById("materiasPorSemestre");
  if (!contenedor) {
    console.error("No se encontró el contenedor 'materiasPorSemestre'");
    return;
  }

  contenedor.innerHTML = "";

  for (const semestreStr of Object.keys(materiasPorSemestre)) {
    const semestre = parseInt(semestreStr);
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
              <td>${m.prerequisitos?.join(', ') || '-'}</td>
              <td>${m.descripcion}</td>
            </tr>
          `).join('')}
        </tbody>
      </table>
    `;

    setTimeout(() => {
      bloque.style.animationDelay = `${semestre * 0.2}s`;
      contenedor.appendChild(bloque);
    }, semestre * 200);
  }
}

// Agrupar materias por semestre
function agruparMateriasPorSemestre(materias) {
  const agrupadas = {};
  materias.forEach(m => {
    const semestre = m.semestre || 1;
    if (!agrupadas[semestre]) agrupadas[semestre] = [];
    agrupadas[semestre].push(m);
  });

  const ordenadas = {};
  Object.keys(agrupadas).sort((a, b) => a - b).forEach(s => {
    ordenadas[s] = agrupadas[s];
  });

  return ordenadas;
}

// Inicializa eventos del modal
function inicializarModal() {
  const modal = document.getElementById("pensumModal");
  const closeBtn = document.getElementById("closeModal");

  closeBtn.addEventListener("click", cerrarModalConAnimacion);

  window.addEventListener("click", e => {
    if (e.target === modal) {
      cerrarModalConAnimacion();
    }
  });
}

function cerrarModalConAnimacion() {
  const modal = document.getElementById("pensumModal");
  const content = document.getElementById("modalContent");

  content.classList.add("fade-out");

  content.addEventListener("animationend", () => {
    modal.style.display = "none";
    content.classList.remove("fade-out");
  }, { once: true });
}

// PATCH Estado
document.addEventListener("click", async e => {
  const icon = e.target.closest(".informe-icon");
  if (!icon) return;

  const pensumItem = icon.closest(".pensum_item");
  if (!pensumItem) return;

  const codigo = pensumItem.querySelector("p").textContent;
  const token = localStorage.getItem("token");

  const pensums = await fetch("/api/pensums", {
    headers: { "Authorization": `Bearer ${token}` }
  }).then(res => res.json());

  const pensum = pensums.find(p => p.codigo === codigo);
  if (!pensum) return;

  Swal.fire({
    title: `¿Estás seguro de activar el Pensum: ${pensum.codigo}?`,
    icon: 'question',
    showCancelButton: true,
    confirmButtonText: 'Sí, Activar',
    cancelButtonText: 'Cancelar'
  }).then(result => {
    if (result.isConfirmed) {
      fetch(`/api/pensums/${pensum.id}/estado`, {
        method: "PATCH",
        headers: {
          "Content-Type": "application/json",
          "Authorization": `Bearer ${token}`
        },
        body: JSON.stringify({ activo: true })
      })
        .then(res => {
          if (!res.ok) throw new Error("Error al activar pensum");
          return res.json();
        })
        .then(data => {
          Swal.fire("Éxito", `Pensum ${data.codigo} activado.`, "success");
          cargarPensums(token); // Recargar la lista actualizada
        })
        .catch(err => {
          console.error(err);
          Swal.fire("Error", "No se pudo activar el pensum.", "error");
        });
    }
  });
});

