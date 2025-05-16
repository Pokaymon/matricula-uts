document.addEventListener("DOMContentLoaded", () => {
    const container = document.querySelector(".perms-container");

function getTokenFromCookies(name = "token") {
  const value = `; ${document.cookie}`;
  const parts = value.split(`; ${name}=`);
  if (parts.length === 2) return parts.pop().split(";").shift();
  return null;
}

    fetch("/api/permisos")
        .then(response => response.json())
        .then(permisos => {
            permisos.forEach(p => {
                const permisoDiv = document.createElement("div");
                permisoDiv.classList.add("permiso");

                const label = document.createElement("label");
                label.classList.add("switch");

                const checkbox = document.createElement("input");
                checkbox.type = "checkbox";
                checkbox.checked = p.estado;
                checkbox.addEventListener("change", () => {
                    const updated = { ...p, estado: checkbox.checked };
                    fetch(`/api/permisos/${p.id}`, {
                        method: "PUT",
                        headers: {

				"Content-Type": "application/json",
				"Authorization": `Bearer ${token}`

			},
                        body: JSON.stringify(updated)
                    })
                    .then(res => {
                        if (!res.ok) throw new Error("Error al actualizar");
                    })
                    .catch(err => {
                        alert("Hubo un problema al actualizar el permiso.");
                        checkbox.checked = !checkbox.checked; // revertir cambio
                    });
                });

                const slider = document.createElement("span");
                slider.classList.add("slider");

                label.appendChild(checkbox);
                label.appendChild(slider);

                const infoDiv = document.createElement("div");
                infoDiv.classList.add("permiso-info");
                infoDiv.innerHTML = `
                    <h3>${p.nombre}</h3>
                    <p>${p.descripcion}</p>
                `;

                permisoDiv.appendChild(infoDiv);
                permisoDiv.appendChild(label);
                container.appendChild(permisoDiv);
            });
        });
});

