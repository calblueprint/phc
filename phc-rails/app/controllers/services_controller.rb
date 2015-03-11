class ServicesController < ApplicationController
  def show
      render :json => Service.services.sort
  end
end
