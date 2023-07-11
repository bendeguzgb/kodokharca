const PARAMETER_SEPARATOR = "&";
const ELEMENTS_IN_ARRAY = "elementsInArray=";
const SEARCH = window.location.search;

// ?elementsInArray=5&
if (SEARCH.match(`^\\?${ELEMENTS_IN_ARRAY}\\d`) === null) {
    console.log(`Search: '${SEARCH}'`)
    console.log("Fixing parameters...")
    let searchCopy = SEARCH;

    if (!searchCopy.includes(ELEMENTS_IN_ARRAY)) {
        searchCopy = `?${ELEMENTS_IN_ARRAY}5${PARAMETER_SEPARATOR}${searchCopy.substring(1)}`
    }

    if (window.location.search !== searchCopy) {
        window.location.search = searchCopy;
    }
}

window.addEventListener("DOMContentLoaded", function() {
    // set selected array size in radio button
    let countOfElements = SEARCH.match(`(&|\\?)?${ELEMENTS_IN_ARRAY}\\d`)[0]
    countOfElements = countOfElements.substring(countOfElements.length - 1);
    document.getElementById(`elements${countOfElements}`).checked = true;


    const elementsInArrayForm = document.getElementById("elementsInArrayContainer");
    elementsInArrayForm.onsubmit = function () {
        for (let input of elementsInArrayForm.getElementsByTagName("input")) {
            if (input.type !== "radio") {
                continue;
            }

            if (input.checked) {
                let number = input.id.replace("elements", "");
                let newElementsInArrayParameter = `${ELEMENTS_IN_ARRAY}${number}`;

                window.location.search = SEARCH.replace(new RegExp(`${ELEMENTS_IN_ARRAY}\\d`), newElementsInArrayParameter);
                return false;
            }
        }
        return false;
    }
});
