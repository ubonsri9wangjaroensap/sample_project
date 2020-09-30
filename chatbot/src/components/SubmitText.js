import React, { Component } from 'react';


class SubmitText extends Component {
     state = {msg:''};

     onFormSubmit = (event)=>{
       event.preventDefault();
       this.props.onSubmit(this.state.msg);
       this.setState({msg:''});
     }
     render(){
       return(
            <div className="ui segment" style={{width:680}}>
                <form onSubmit={this.onFormSubmit} className="ui form">
                    <input type="text" style={{width:650}} value = {this.state.msg} onChange={(e) =>this.setState({msg:e.target.value})}/>
                </form>
            </div>
       );
     }

}

export default SubmitText;
