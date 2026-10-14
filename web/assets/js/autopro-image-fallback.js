(function () {
    function normalize(text) {
        return (text || "")
                .toString()
                .normalize("NFD")
                .replace(/[\u0300-\u036f]/g, "")
                .toLowerCase();
    }

    function pickFallback(name, src) {
        var key = normalize(name + " " + src);
        if (key.indexOf("toyota") !== -1 && key.indexOf("camry") !== -1) {
            return "toyota-camry.jpg";
        }
        if (key.indexOf("ford") !== -1 && key.indexOf("ranger") !== -1) {
            return "ford-ranger.jpg";
        }
        if (key.indexOf("mazda") !== -1 && (key.indexOf("cx-5") !== -1 || key.indexOf("cx5") !== -1)) {
            return "mazda-cx5.jpg";
        }
        if (key.indexOf("bmw") !== -1 && (key.indexOf("320") !== -1 || key.indexOf("3 series") !== -1)) {
            return "bmw-320i.jpg";
        }
        if (key.indexOf("hyundai") !== -1 && key.indexOf("santa") !== -1) {
            return "hyundai-santafe.jpg";
        }
        if (key.indexOf("audi") !== -1 && key.indexOf("q5") !== -1) {
            return "audi-q5.jpg";
        }
        if (key.indexOf("honda") !== -1 && key.indexOf("cr-v") !== -1) {
            return "honda-crv.jpg";
        }
        if (key.indexOf("mercedes") !== -1 && (key.indexOf("c200") !== -1 || key.indexOf("c class") !== -1)) {
            return "mercedes-c-class.jpg";
        }
        if (key.indexOf("toyota") !== -1 && key.indexOf("vios") !== -1) {
            return "toyota-vios.jpg";
        }
        if (key.indexOf("vinfast") !== -1 && key.indexOf("vf8") !== -1) {
            return "vinfast-vf8.jpg";
        }
        return "car-placeholder.svg";
    }

    window.useAutoProCarFallback = function (img) {
        if (!img || img.dataset.fallbackApplied === "true") {
            return;
        }
        img.dataset.fallbackApplied = "true";
        img.onerror = null;
        var contextPath = img.dataset.contextPath || "";
        var file = pickFallback(img.dataset.carName || img.alt, img.getAttribute("src"));
        img.src = contextPath + "/uploads/cars/" + file;
        img.classList.add("autopro-image-fallback");
    };

    function repairBrokenImages() {
        document.querySelectorAll("img[data-context-path]").forEach(function (img) {
            if (img.complete && img.naturalWidth === 0) {
                window.useAutoProCarFallback(img);
            }
        });
    }

    if (document.readyState === "loading") {
        document.addEventListener("DOMContentLoaded", repairBrokenImages);
    } else {
        repairBrokenImages();
    }
}());
