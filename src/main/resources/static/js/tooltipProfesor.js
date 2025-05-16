document.addEventListener("DOMContentLoaded", () => {
  const tooltip = document.getElementById("tooltip-profesor");

function getTokenFromCookies(name = "token") {
  const value = `; ${document.cookie}`;
  const parts = value.split(`; ${name}=`);
  if (parts.length === 2) return parts.pop().split(";").shift();
  return null;
}

  document.querySelectorAll(".profesor-cedula").forEach(td => {
    td.addEventListener("mouseenter", async (e) => {
      const cedula = td.dataset.cedula;

      try {

	const token = getTokenFromCookies();
	if (!token) {
	  console.warn("Token JWT No encontrado en las Cookies");
	  return;
	}

        const res = await fetch(`/api/users/profesores/${cedula}`, {
	  headers: {
	    "Authorization": `Bearer ${token}`
	  }
	});

	if (!res.ok) return;

        const profesor = await res.json();
        tooltip.innerHTML = `
          <strong>${profesor.nombre} ${profesor.apellido}</strong><br>
          Cédula: ${profesor.cedula}<br>
        `;
        tooltip.style.top = `${e.pageY + 10}px`;
        tooltip.style.left = `${e.pageX + 10}px`;
        tooltip.classList.remove("hidden");
        tooltip.classList.add("show");
      } catch (err) {
        console.error("Error al obtener datos del profesor", err);
      }
    });

    td.addEventListener("mouseleave", () => {
      tooltip.classList.remove("show");
      tooltip.classList.add("hidden");
    });

    td.addEventListener("mousemove", (e) => {
      tooltip.style.top = `${e.pageY + 10}px`;
      tooltip.style.left = `${e.pageX + 10}px`;
    });
  });
});
