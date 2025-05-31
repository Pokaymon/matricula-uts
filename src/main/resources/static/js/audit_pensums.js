document.addEventListener("DOMContentLoaded", () => {
  cargarPensums();
});

const token = localStorage.getItem("token");

function cargarPensums() {
  fetch("/api/pensums", {
      headers: {
        "Authorization": token
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
      // abrirModalConDetalles(pensum.codigo);
      alert("Pensum seleccionado: " + pensum.codigo);
    }
  });

  return div;
}
