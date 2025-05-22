document.addEventListener("DOMContentLoaded", () => {
  fetch("/api/grupos/vista")
    .then(res => res.json())
    .then(data => {
      const groupsContainer = document.querySelector(".groups");

      data.forEach(grupo => {
        const grupoDiv = document.createElement("div");
        grupoDiv.classList.add("group_item");
        Object.assign(grupoDiv.style, {
          display: "flex",
          justifyContent: "space-between",
          width: "100%"
        });

        grupoDiv.innerHTML = `
          <p>${grupo.codigo}</p>
          <p>${grupo.nombreMateria}</p>
        `;

        groupsContainer.appendChild(grupoDiv);
      });
    })
    .catch(err => {
      console.error("Error cargando grupos:", err);
    });
});
