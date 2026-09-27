const PARAMETER_SEPARATOR = "&";
const DASH_SEPARATOR = "-";
const QUESTION_SEPARATOR = ",";
const NUMBER_LOCATION = "numberLocation";
const QUESTION_STRING = "feltettKerdesek=";
const SEARCH = window.location.search;


window.addEventListener("DOMContentLoaded", function() {
    // override 'onsubmit' functions of questions
    const questionForms = document.getElementsByClassName('question');
    for (let form of questionForms) {
        form.onsubmit = questionOnSubmit;
    }

    // player notes red X on click
    const numberCells = document.querySelectorAll(".number-cell");
    for (let numberCell of numberCells) {
        numberCell.addEventListener("click", playerNotesRedXOnClick);
    }

    // add change listener to guess color pickers
    const guessColorSelectors = document.getElementsByClassName("guessColorSelector");
    for (let guessColorSelector of guessColorSelectors) {
        guessColorSelector.addEventListener("change", guessSelectorOnChange);
    }

    // override 'onsubmit' function of guessing
    const guessForm = document.getElementById("guess");
    guessForm.onsubmit = questionOnSubmit;
});


const questionOnSubmit = function() {
    const questionID = this.id;
    const questionName = this.id.split(DASH_SEPARATOR)[0];

    let questionIndex;
    let playersQuestions;
    let parameters = SEARCH.split(PARAMETER_SEPARATOR);

    for (let i= 0; i < parameters.length; i++) {
        if (parameters[i].includes(QUESTION_STRING)) {
            playersQuestions = parameters[i];
            questionIndex = i;
            break;
        }
    }

    if (playersQuestions === undefined) {
        parameters.push(QUESTION_STRING); // separator not needed bc of join
        playersQuestions = QUESTION_STRING;
        questionIndex = parameters.length - 1;
    }

    if (isAlreadyAskedQuestion(playersQuestions, questionName)) {
        alert(`Hiba! Ezt a kérdést már feltetted: '${questionName}'\nKérdések: '${playersQuestions}'`);
        return false;
    }

    let question = (playersQuestions.endsWith("=")) ? questionName : (QUESTION_SEPARATOR + questionName);

    if (questionID.startsWith(NUMBER_LOCATION)) {
        const radioButtons = document.querySelectorAll(`#${questionID} input[type=radio], #${questionID} [type=hidden]`);

        for (const radioButton of radioButtons) {
            if (radioButton.checked) {
                question += DASH_SEPARATOR;
                question += radioButton.id.at(-1);
                break;
            }
        }
    } else if (questionID === "guess") {
        const selects = document.getElementsByTagName("select");
        let guessNumbers = "";
        let guessColors = "";

        for (let i = 0; i < selects.length / 2; i++) {
            let select = selects[i];
            let selectedValue = select[select.selectedIndex].value;

            guessNumbers += selectedValue;
        }

        for (let i = selects.length / 2; i < selects.length; i++) {
            let colors = {"BLACK": "k", "WHITE": "h", "GREEN": "z"};
            let select = selects[i];
            let selectedValueClasses = select[select.selectedIndex].classList;

            for (let selectedValueClass of selectedValueClasses) {
                let colorValue = colors[selectedValueClass];

                if (colorValue !== undefined) {
                    guessColors += colorValue;
                    break;
                }
            }
        }

        question += DASH_SEPARATOR;
        question += guessNumbers;
        question += DASH_SEPARATOR;
        question += guessColors;
    }


    parameters[questionIndex] = playersQuestions + question;
    window.location.search = parameters.join(PARAMETER_SEPARATOR);

    return false;
};

const playerNotesRedXOnClick = function () {
    const redX = this.querySelector(".red-x");
    const display = window.getComputedStyle(redX).display;

    if (display === "none") {
        redX.style.display = "block";
    } else {
        redX.style.display = "none";
    }
}

const guessSelectorOnChange = function () {
    const guessColorSelector = this;
    const selectedOption = guessColorSelector.options[guessColorSelector.selectedIndex];

    guessColorSelector.style.backgroundColor = window.getComputedStyle(selectedOption).backgroundColor;
}

const isAlreadyAskedQuestion = function (playersQuestions, questionName) {
    if (questionName.startsWith(NUMBER_LOCATION)) {
        // eg. questionName = "numberLocation-34"
        const firstOption  = `${NUMBER_LOCATION}-${questionName.charAt(questionName.length-1)}`; // numberLocation-4
        const secondOption = `${NUMBER_LOCATION}-${questionName.charAt(questionName.length-2)}`; // numberLocation-3

        return playersQuestions.includes(firstOption) || playersQuestions.includes(secondOption);
    }

    return playersQuestions.includes(questionName);
}