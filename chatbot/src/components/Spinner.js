import React from 'react';
import '../App.css';
const Spinner = (props) => {
  console.log(props);
  if(!props.show){
    return null;
  }
  return (
    <div className="ui container grid">
    <div className="ui row">
    <div className="column two wide"></div>
    <div className="column five wide">
        <div class="ui active centered inline loader"></div>
    </div>
    </div>
    </div>
  );
};

export default Spinner;
