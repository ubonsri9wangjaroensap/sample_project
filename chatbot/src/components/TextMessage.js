import React from 'react';
import '../App.css';
const TextMessage = (props) => {
  console.log(props);
  return (
    <div className="ui container grid">
    <div className="ui row">
    <div className="column two wide">
    <div className="you-logo">You</div>
    </div>
    <div className="column eight wide">
    <div className = "ui input"><input style={{width:400}} value={props.message}/>
    </div>
    </div>
    </div>
    </div>
  );
};

export default TextMessage;
