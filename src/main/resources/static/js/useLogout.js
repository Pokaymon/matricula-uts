document.addEventListener("DOMContentLoaded", () => {
  const logoutBtn = document.getElementById("logout-btn");

  if (logoutBtn) {
    logoutBtn.addEventListener("click", function (e) {
      e.preventDefault(); // Evita que se recargue la página

      // Eliminar token del localStorage
      localStorage.removeItem("token");

      // Redirigir al login
      window.location.href = "/login";
    });
  }
});
