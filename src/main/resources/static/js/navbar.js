<script>
    function toggleMenu() {
      document.getElementById("navbar").classList.toggle("active");
    }

    function toggleDropdown() {
      const dropdown = document.getElementById("dropdown-menu");
      dropdown.style.display = dropdown.style.display === "block" ? "none" : "block";
    }

    function handleClientPage() {
      window.location.href = "/cliente/billeteras";
    }
</script>
