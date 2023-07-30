const PARAMETER_SEPARATOR = "&";
const ELEMENTS_IN_ARRAY = "elementsInArray=";
const FILTER_STRING = "filter=";
const SEARCH = window.location.search;

// ?elementsInArray=5&filter=
if (SEARCH.match(`^\\?${ELEMENTS_IN_ARRAY}\\d${PARAMETER_SEPARATOR}${FILTER_STRING}`) === null) {
    console.log(`Search: '${SEARCH}'`)
    console.log("Fixing parameters...")
    let searchCopy = SEARCH;

    if (!searchCopy.includes(ELEMENTS_IN_ARRAY)) {
        searchCopy = `?${ELEMENTS_IN_ARRAY}5${PARAMETER_SEPARATOR}${searchCopy.substring(1)}`
    }

    if (!searchCopy.includes(FILTER_STRING)) {
        let param = (searchCopy.endsWith(PARAMETER_SEPARATOR)) ? "" : PARAMETER_SEPARATOR;
        param += FILTER_STRING;
        searchCopy += param;
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

    // override 'onsubmit' of the selector of number of player's numbers
    const elementsInArrayForm = document.getElementById("elementsInArrayForm");
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

    // override 'onsubmit' functions of questions
    for (let form of document.getElementsByClassName('question')) {
        form.onsubmit = function() {
            const id = this.id;

            let filterIndex;
            let currentFilters;
            let parameters = SEARCH.split(PARAMETER_SEPARATOR);

            for (let i= 0; i < parameters.length; i++) {
                if (parameters[i].includes(FILTER_STRING)) {
                    currentFilters = parameters[i];
                    filterIndex = i;
                    break;
                }
            }

            if (currentFilters.includes(id)) {
                alert(`Ez a szűrés már megtörtént: '${id}'\nKeresések: '${currentFilters}'`);
                return false;
            }

            let filter = (currentFilters.endsWith("=")) ? id : `,${id}`;
            let isRadioChecked = false;

            for (let input of this.getElementsByTagName("input")) {
                if (input.type === "submit" || isRadioChecked) {
                    continue;
                }

                if (input.type === "radio") {
                    let isOn = input.checked;
                    let isFound = ["páros", "fekete", "igen"].includes(input.labels[0].innerText.toLowerCase());

                    filter += `-${isFound && isOn || !isFound && !isOn}`;
                    isRadioChecked = true;
                } else {
                    filter += `-${input.value}`;

                }
            }

            parameters[filterIndex] = currentFilters + filter;
            window.location.search = parameters.join(PARAMETER_SEPARATOR);

            return false;
        };
    }
});
